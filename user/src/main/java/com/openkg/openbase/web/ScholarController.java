package com.openkg.openbase.web;

import com.openkg.openbase.constant.Msg;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Scholar;
import com.openkg.openbase.service.ScholarService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Api(description = "openbase graphScan api", tags = "graphScan api",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping(value="scholar")
@CrossOrigin
public class ScholarController {

    private ScholarService scholarService;

    @Autowired
    public void setScholarService(ScholarService scholarService) {
        this.scholarService = scholarService;
    }

    //图谱浏览
    @RequestMapping(value="scholarInfo", method = RequestMethod.GET)
    public Res scholarInfo(String id){//学者id（学者triple中的subject）
        Res res = new Res();
        Scholar scholar = new Scholar(id);
        boolean result = scholarService.parseInfo(scholar);
        if(result){
            res.setCode(Msg.SUCCESS.getCode());
            res.setMsg(Msg.SUCCESS.getMsg());
            res.setData(scholar);
        }else {
            res.setCode(Msg.FAILED.getCode());
            res.setMsg(Msg.FAILED.getMsg());
        }
        return res;
    }
}
