package com.synram.morningbucket.Modal;

public class AcountListModal {
    int menu_image;
    String menu_title;


    public int getMenu_image() {
        return menu_image;
    }

    public void setMenu_image(int menu_image) {
        this.menu_image = menu_image;
    }

    public String getMenu_title() {
        return menu_title;
    }

    public void setMenu_title(String menu_title) {
        this.menu_title = menu_title;
    }


    public AcountListModal(int menu_image, String menu_title) {
        this.menu_image = menu_image;
        this.menu_title = menu_title;
    }
}
