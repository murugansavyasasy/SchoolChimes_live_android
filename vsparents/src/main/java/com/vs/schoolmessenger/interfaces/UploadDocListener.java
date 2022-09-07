package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.StaffList;
import com.vs.schoolmessenger.model.UploadFilesModel;

public interface UploadDocListener {
    public void Doc_addClass(UploadFilesModel student);
    public void Doc_removeClass(UploadFilesModel student);
}
