package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class SkillAttachmentModel implements Serializable {

    String ContentId;
    String SkillId;
    String Attachment;
    String Type;
    String Order;

    public SkillAttachmentModel(String ContentId, String Attachment, String Type, String Order, String skillId) {
        this.SkillId = skillId;
        this.ContentId = ContentId;
        this.Attachment = Attachment;
        this.Type = Type;
        this.Order = Order;
    }

    public String getContentId() {
        return ContentId;
    }

    public void setContentId(String contentId) {
        ContentId = contentId;
    }

    public String getSkillId() {
        return SkillId;
    }

    public void setSkillId(String skillId) {
        SkillId = skillId;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String attachment) {
        Attachment = attachment;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }
}


