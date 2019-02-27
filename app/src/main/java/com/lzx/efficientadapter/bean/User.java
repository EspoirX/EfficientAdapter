package com.lzx.efficientadapter.bean;


import com.lzx.library.MultiTypeEntity;

public class User implements MultiTypeEntity<User> {
    public static int TYPE_USER = 0x004;
    private String name;
    private String phone;
    private int age;
    private int avatarRes;

    public User(String name, int age, int avatarRes, String phone) {
        this.name = name;
        this.age = age;
        this.avatarRes = avatarRes;
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public int getAvatarRes() {
        return avatarRes;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public int getItemType() {
        return TYPE_USER;
    }

    @Override
    public boolean areItemsTheSame(User newItem) {
        return name.equals(newItem.name);
    }

    @Override
    public boolean areContentsTheSame(User newItem) {
        return true;
    }
}
