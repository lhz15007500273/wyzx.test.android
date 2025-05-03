package com.example.wyzx.models;

public class CommodityModel {
    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品类别id
     */
    private Integer commodityType;

    /**
     * 商品价格
     */
    private Double commodityPrice;

    /**
     * 商品总量
     */
    private Long commodityTotal;

    /**
     * 商品描述
     */
    private String commodityInfo;

    /**
     * 商品图片(Base64编码)
     */
    private String commodityImg;

    /**
     * 商品其他图片Url
     */
    private String commodityOtherImgUrls;

    // 构造方法
    public CommodityModel(Long id, String name, Double price, String img, String otherImgUrls) {
        this.id = id;
        this.commodityName = name;
        this.commodityPrice = price;
        this.commodityImg = img;
        this.commodityOtherImgUrls = otherImgUrls;
    }

    public CommodityModel() {
    }

    /**
     * 商品id
     *
     * @return id 商品id
     */
    public Long getId() {
        return id;
    }

    /**
     * 商品id
     *
     * @param id 商品id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 商品名称
     *
     * @return commodity_name 商品名称
     */
    public String getCommodityName() {
        return commodityName;
    }

    /**
     * 商品名称
     *
     * @param commodityName 商品名称
     */
    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    /**
     * 商品类别id
     *
     * @return commodity_type 商品类别id
     */
    public Integer getCommodityType() {
        return commodityType;
    }

    /**
     * 商品类别id
     *
     * @param commodityType 商品类别id
     */
    public void setCommodityType(Integer commodityType) {
        this.commodityType = commodityType;
    }

    /**
     * 商品价格
     *
     * @return commodity_price 商品价格
     */
    /**
     * 商品价格
     *
     * @return 格式化后的价格字符串，如"18.00"
     */
    public Double getCommodityPrice() {
        return commodityPrice;
    }
    /**
     * 商品价格
     *
     * @param commodityPrice 商品价格
     */
    public void setCommodityPrice(Double commodityPrice) {
        this.commodityPrice = commodityPrice;
    }


    /**
     * 商品描述
     *
     * @return commodity_info 商品描述
     */
    public String getCommodityInfo() {
        return commodityInfo;
    }

    /**
     * 商品描述
     *
     * @param commodityInfo 商品描述
     */
    public void setCommodityInfo(String commodityInfo) {
        this.commodityInfo = commodityInfo;
    }

    /**
     * 商品图片(Base64编码)
     *
     * @return commodity_img 商品图片(Base64编码)
     */
    public String getCommodityImg() {
        return commodityImg;
    }

    /**
     * 商品图片(Base64编码)
     *
     * @param commodityImg 商品图片(Base64编码)
     */
    public void setCommodityImg(String commodityImg) {
        this.commodityImg = commodityImg;
    }

    /**
     * 商品其他图片Url
     *
     * @return commodity_other_img_urls 商品其他图片Url
     */
    public String getCommodityOtherImgUrls() {
        return commodityOtherImgUrls;
    }

    /**
     * 商品其他图片Url
     *
     * @param commodityOtherImgUrls 商品其他图片Url
     */
    public void setCommodityOtherImgUrls(String commodityOtherImgUrls) {
        this.commodityOtherImgUrls = commodityOtherImgUrls;
    }
}
