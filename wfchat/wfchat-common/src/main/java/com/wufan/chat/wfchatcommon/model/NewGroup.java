package com.wufan.chat.wfchatcommon.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wufan
 * @Date: 2019/7/12 14:56
 */
public class NewGroup implements Serializable {
    private static final long serialVersionUID = 739328207935678452L;
    String groupname;
    List<String> members;

    public NewGroup(String groupname, List<String> members) {
        this.groupname = groupname;
        this.members = members;
    }

    public NewGroup() {
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
