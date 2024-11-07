package com.vs.schoolmessenger.model;

import java.util.List;

public class StaffNoticeBoard {


    private int status;

    private String message;

    private List<StaffNoticeBoard.StaffNoticeBoardData> data;


    public void setStatus(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String id) {
        this.message = id;
    }

    public String getMessage() {
        return message;
    }


    public void setData(List<StaffNoticeBoard.StaffNoticeBoardData> data) {
        this.data = data;
    }

    public List<StaffNoticeBoard.StaffNoticeBoardData> getData() {
        return data;
    }

    public class StaffNoticeBoardData {


        private String homeworktopic;

        private String homeworkcontent;

        private String subjectid;

        private String subjectname;
        private String createdby;

        private List<StaffNoticeBoard.StaffNoticeBoardData.StaffNoticeBoardFilePath> file_path;

        public String getHomeworktopic() {
            return homeworktopic;
        }

        public void setHomeworktopic(String homeworktopic) {
            this.homeworktopic = homeworktopic;
        }

        public String getHomeworkcontent() {
            return homeworkcontent;
        }

        public void setHomeworkcontent(String homeworkcontent) {
            this.homeworkcontent = homeworkcontent;
        }

        public String getSubjectid() {
            return subjectid;
        }

        public void setSubjectid(String subjectid) {
            this.subjectid = subjectid;
        }

        public String getSubjectname() {
            return subjectname;
        }

        public void setSubjectname(String subjectname) {
            this.subjectname = subjectname;
        }

        public String getCreatedby() {
            return createdby;
        }

        public void setCreatedby(String createdby) {
            this.createdby = createdby;
        }

        public List<StaffNoticeBoard.StaffNoticeBoardData.StaffNoticeBoardFilePath> getFile_path() {
            return file_path;
        }

        public void setFile_path(List<StaffNoticeBoard.StaffNoticeBoardData.StaffNoticeBoardFilePath> file_path) {
            this.file_path = file_path;
        }

        public class StaffNoticeBoardFilePath {

            private String path;

            private String type;


            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
