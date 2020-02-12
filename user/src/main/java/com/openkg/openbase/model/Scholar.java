package com.openkg.openbase.model;

import lombok.Data;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

@Data
public class Scholar {
    String id;
    public static Model model;
    private double hindex;
    private String name;
    private String nameZh;
    private int totalCitation;
    private String homepage;
    private double activity;
    private String gender;
    private String nationality;
    private String jobTitle;
    private int pubNumber;
    private String email;
    private String bio;

    static{
        model = ModelFactory.createDefaultModel();
        model.read("kg4ai.json", "JSON-LD");
    }

    public Scholar(String sName) {
        //将带前缀的id恢复为绝对IRI
        if(sName.contains("profile:")){
            sName = sName.replace("profile:", "https://www.aminer.cn/profile/");
        }
        this.id = sName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Model getModel() {
        return model;
    }

    public static void setModel(Model model) {
        Scholar.model = model;
    }

    public double getHindex() {
        return hindex;
    }

    public void setHindex(double hindex) {
        this.hindex = hindex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public int getTotalCitation() {
        return totalCitation;
    }

    public void setTotalCitation(int totalCitation) {
        this.totalCitation = totalCitation;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public double getActivity() {
        return activity;
    }

    public void setActivity(double activity) {
        this.activity = activity;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getPubNumber() {
        return pubNumber;
    }

    public void setPubNumber(int pubNumber) {
        this.pubNumber = pubNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
