package com.openkg.openbase.service;

import com.openkg.openbase.model.Scholar;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

@Service
public class ScholarService {
    //根据学者id查询其他信息
    public boolean parseInfo(Scholar scholar) {
        String qStr = "PREFIX  : <http://cnschema.org/> "
                + "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "Select ?h ?n ?nz ?t ?hp ?a ?g  ?job ?pnbr ?em ?b where{<"
                + scholar.getId() + ">  :hIndex ?h. <"
                + scholar.getId() + "> :name ?n. <"
                + scholar.getId() + "> :nameZh ?nz. <"
                + scholar.getId() + "> :totalCitation ?t. <"
                + scholar.getId() + "> :homepage ?hp. <"
                + scholar.getId() + "> :activity ?a. <"
                + scholar.getId() + "> :gender ?g. <"
//				+ id.toString() + "> :nationality [:name ?name]. <"
                + scholar.getId() + "> :jobTitle ?job. <"
                + scholar.getId() + "> :pubNumber ?pnbr. <"
                + scholar.getId() + "> :email ?em. <"
                + scholar.getId() + "> :bio ?b. }";

        Query query = QueryFactory.create(qStr);
        QueryExecution qe = QueryExecutionFactory.create(query, Scholar.model);
        ResultSet results = qe.execSelect();
        if (results.hasNext()) {
            QuerySolution qs = results.next();
            double y = qs.getLiteral("h").getDouble();
            String name = qs.getLiteral("n").getString();
            String nameZh = qs.getLiteral("nz").getString();
            int totalCitation = qs.getLiteral("t").getInt();
            String homepage = qs.getLiteral("hp").getString();
            double activity = qs.getLiteral("a").getDouble();
            String gender = qs.getLiteral("g").getString();
//			String nationality = qs.getLiteral("name").getString();
            String jobTitle = qs.getLiteral("job").getString();
            int pubNumber = qs.getLiteral("pnbr").getInt();
            String email = qs.getLiteral("em").getString();
            String bio = qs.getLiteral("b").getString();
            scholar.setHindex(y);
            scholar.setName(name);
            scholar.setNameZh(nameZh);
            scholar.setTotalCitation(totalCitation);
            scholar.setHomepage(homepage);
            scholar.setActivity(activity);
            scholar.setGender(gender);
            scholar.setNationality("china");
            scholar.setJobTitle(jobTitle);
            scholar.setPubNumber(pubNumber);
            scholar.setEmail(email);
            scholar.setBio(bio);
            return true;
        }
        return false;
    }
}
