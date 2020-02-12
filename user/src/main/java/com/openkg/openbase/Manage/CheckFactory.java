package com.openkg.openbase.Manage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.DataStream.Element;
import com.openkg.openbase.model.DataStream.OriginTriple;
import com.openkg.openbase.model.JobBase;
import com.openkg.openbase.model.historyModule.EntityHistory;
import com.openkg.openbase.model.historyModule.TripleHistory;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by mi on 18-10-20.
 */
public class CheckFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(CheckFactory.class);
    private final static Element element = new Element();

    private static final String USERSOURCE = "OpenbaseCheckUserSource";
    private static final String CHECK_TRIPLE_HISTORY = "OpenbaseCheckTripleHistory";
    private static final String CHECK_ENTITY_HISTORY = "OpenbaseCheckEntityHistory";
    private static final MongoCollection<Document> us = Singleton.mongoDBUtil.getdb().getCollection(USERSOURCE);
    private static final MongoCollection<Document> th = Singleton.mongoDBUtil.getdb().getCollection(CHECK_TRIPLE_HISTORY);
    private static final MongoCollection<Document> eh = Singleton.mongoDBUtil.getdb().getCollection(CHECK_ENTITY_HISTORY);


    //获取实体统计总量
    public static Long getTripleCountByUid(Integer uid) {
        Document match = new Document("uid", uid);
        return th.count(match);
    }

    //获取关系统计总量
    public static Long getEntityCountByUid(Integer uid) {
        Document match = new Document("uid", uid);
        return eh.count(match);
    }


    //保存三元组验收记录
    public static Boolean saveRelationHistory(TripleHistory rth) {
        try {
            Document match = new Document("uid", rth.getUid());
            match.put("jobId", rth.getJobId());
            match.put("tripleId", rth.getTripleId());

            Document update = new Document();
            update.put("$set", new Document("op", rth.getOp()));

            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
            options.upsert(true);
            th.findOneAndUpdate(match, update, options);
            return true;
        } catch (Exception e) {
            LOGGER.error("error in save triple history!", e);
            return false;
        }
    }

    //保存实体审核记录
    public static Boolean saveEntityHistory(EntityHistory reh) {
        try {
            Document match = new Document("uid", reh.getUid());
            match.put("jobId", reh.getJobId());
            match.put("entityId", reh.getEntityId());

            Document update = new Document();
            update.put("$set", new Document("op", reh.getOp()));

            FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
            options.upsert(true);
            eh.findOneAndUpdate(match, update, options);
            return true;
        } catch (Exception e) {
            LOGGER.error("error in save entity history!", e);
            return false;
        }

    }

    //获取用户三元组验收记录
    public static List<TripleHistory> getCheckTripleHistory(Integer jobId, Integer uid) {
        List<TripleHistory> res = new ArrayList<>();
        Document match = new Document("jobId", jobId);
        match.put("uid", uid);
        FindIterable<Document> findIterable = th.find(match);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document one = mongoCursor.next();
            Long tripleId = one.getLong("tripleId");
            res.add(new TripleHistory(one.getInteger("uid"),one.getInteger("jobId"),tripleId,one.getInteger("op")));
        }

        return res;

    }

    //获取用户实体验收记录
    public static List<EntityHistory> getCheckEntityHistory(Integer jobId, Integer uid) {
        List<EntityHistory> res = new ArrayList<>();
        Document match = new Document("jobId", jobId);
        match.put("uid", uid);
        FindIterable<Document> findIterable = eh.find(match);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document one = mongoCursor.next();
            res.add(Singleton.GSON.fromJson(one.toJson(), EntityHistory.class));
        }

        return res;

    }


    public static List<Element> getElementListFromRecordHistory(List<JobBase.TaskRecord> taskList, JobBase.JobType type){
        List<Element> result_element_list = new ArrayList<>();
        switch (type.getName()){
            case "entity":
                for (JobBase.TaskRecord oneRecord : taskList) {
                    Element element = new Element(oneRecord.getEntityID(), JobFactory.retrieveAtNameValue(oneRecord.getEntityID()), oneRecord.getOpType().getCode(), JobFactory.retrieveSummaryValue(oneRecord.getEntityID()));
                    result_element_list.add(element);
                }
                return result_element_list;

            case "triple":
                Map<String, Element> aggregate = new HashMap<>();
                for (JobBase.TaskRecord oneRecord : taskList) {
                    //三元组格式转换,由datacenter格式转换成接口定义格式

                    Element.Triple inerTriple = null;
                    inerTriple = element.new Triple( oneRecord.getEntityID()+"_"+oneRecord.getPropertyName(),oneRecord.getPropertyName(), JobFactory.retrievePropertyValue(oneRecord.getEntityID(), oneRecord.getPropertyName()), oneRecord.getOpType().getCode());

                    //聚合三元组
                    if (!aggregate.containsKey(oneRecord.getEntityID())) {
                        List<Element.Triple> eTriples = new ArrayList<>();
                        eTriples.add(inerTriple);
                        aggregate.put(oneRecord.getEntityID(), new Element(oneRecord.getEntityID(), JobFactory.retrieveAtNameValue(oneRecord.getEntityID()), eTriples));
                    } else {
                        aggregate.get(oneRecord.getEntityID()).getTriples().add(inerTriple);
                    }
                }
                for (String entId : aggregate.keySet()) {
                    result_element_list.add(aggregate.get(entId));
                }
                return result_element_list;

        }
        return null;
    }
}
