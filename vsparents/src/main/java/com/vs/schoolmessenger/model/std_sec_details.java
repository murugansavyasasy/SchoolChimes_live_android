package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class std_sec_details {

     private String class_id;
     private String section_id;

     public String getClass_id() {
          return class_id;
     }

     public void setClass_id(String class_id) {
          this.class_id = class_id;
     }

     public String getSection_id() {
          return section_id;
     }

     public void setSection_id(String section_id) {
          this.section_id = section_id;
     }

     @Override
     public String toString() {
          return "std_sec_details{" +
                  "class_id=" + class_id +
                  ", section_id=" + section_id +
                  '}';
     }

     public std_sec_details(String class_id, String section_id) {
          this.class_id = class_id;
          this.section_id = section_id;
     }
}
