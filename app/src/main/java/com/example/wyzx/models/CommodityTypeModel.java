package com.example.wyzx.models;

public class CommodityTypeModel {
    private Long id;
    private String name;
    private int iconResId; // 新增图标资源ID字段

    // 修改构造方法，允许 long 类型自动转换为 Long
    public CommodityTypeModel(long id, String name, int iconResId) {
        this.id = id; // 自动装箱，long -> Long
        this.name = name;
        this.iconResId = iconResId;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    @Override
    public String toString() {
        return "CommodityTypeModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", iconResId=" + iconResId +
                '}';
    }
}
