package com.xin.test.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
public class TestBean {
    private List<row> rows;
    private String title;

    public List<row> getRows() {
        return rows;
    }

    public void setRows(List<row> rows) {
        this.rows = rows;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public class row{
        String description;
        String imageHref;
        String title;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageHref() {
            return imageHref;
        }

        public void setImageHref(String imageHref) {
            this.imageHref = imageHref;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
