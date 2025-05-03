package com.example.wyzx.models;

/**
 * 购物车或已购买商品Model
 */
public class CartModel {
    private Long id;                 // 商品ID
    private String commodityName;    // 商品名称
    private Integer commodityType;   // 商品类别
    private Double commodityPrice;   // 商品单价
    private Integer number;          // 购买数量
    private Double totalPrice;       // 总价
    private String commodityImg;     // 商品图片(Base64)
    private String commodityInfo;    // 商品描述
    private boolean choosed;         // 是否选中

    public CartModel(Long id, String name, Double price, Integer quantity, String image, String description) {
        this.id = id;
        this.commodityName = name;
        this.commodityPrice = price != null ? price : 0.0; // 防止空指针，默认价格为0
        this.number = quantity != null ? quantity : 1; // 防止空指针，默认数量为1
        this.commodityImg = image;
        this.commodityInfo = description;
        calculateTotalPrice();  // 在构造函数中计算总价
    }

    public CartModel() {
        this.commodityPrice = 0.0; // 默认价格为0
        this.number = 1; // 默认数量为1
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public Integer getCommodityType() {
        return commodityType;
    }

    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }

    public Double getCommodityPrice() {
        return commodityPrice;
    }

    public void setCommodityPrice(Double commodityPrice) {
        this.commodityPrice = commodityPrice != null ? commodityPrice : 0.0;  // 确保不为null
        calculateTotalPrice();  // 更新价格时重新计算总价
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number != null ? number : 1;  // 确保数量不为null
        calculateTotalPrice();  // 更新数量时重新计算总价
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    private void calculateTotalPrice() {
        this.totalPrice = this.commodityPrice * this.number;  // 计算总价
    }

    public String getCommodityImg() {
        return commodityImg;
    }

    public void setCommodityImg(String commodityImg) {
        this.commodityImg = commodityImg;
    }

    public String getCommodityInfo() {
        return commodityInfo;
    }

    public void setCommodityInfo(String commodityInfo) {
        this.commodityInfo = commodityInfo;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
