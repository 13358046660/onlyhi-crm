package cn.onlyhi.client.po;

import java.util.Date;

/**
 * 用户设备信息
 * @author shitongtong
 * Created by shitongtong on 2018/02/27
 */
public class UserDeviceInformation extends BasePo {
    private Integer id;

    /**
     * 电脑型号
     */
    private String computerModel;

    /**
     * 操作系统
     */
    private String operationSystem;

    /**
     * 处理器
     */
    private String processor;

    /**
     * 主板
     */
    private String motherboard;

    /**
     * 内存
     */
    private String ram;

    /**
     * 硬盘
     */
    private String hardDisk;

    /**
     * 显卡
     */
    private String graphicsCard;

    /**
     * 显示器
     */
    private String monitor;

    /**
     * 光驱
     */
    private String opticalDrive;

    /**
     * 声卡
     */
    private String soundCard;

    /**
     * 网卡
     */
    private String networkCard;

    /**
     * 电池
     */
    private String battery;

    /**
     * 系统版本
     */
    private String systemVersion;

    /**
     * 计算机名
     */
    private String computerName;

    /**
     * 系统安装日期
     */
    private String systemInstallationDate;

    /**
     * 系统启动时间
     */
    private String systemStartupTime;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 系统语言
     */
    private String systemLanguage;

    /**
     * 系统架构
     */
    private String systemStructure;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * mac地址
     */
    private String macAddress;

    /**
     * 麦克风测试结果
     */
    private String microphoneEquipment;

    /**
     * 扬声器测试结果
     */
    private String speakerEquipment;

    /**
     * 摄像头测试结果
     */
    private String cameraEquipment;

    /**
     * 手写板测试结果
     */
    private String tabletDevice;

    /**
     * 最近一次检测时间
     */
    private String lastTestTime;

    /**
     * 客户端类型: ANDROID、IOS、PC、APAD、IPAD等等
     */
    private String deviceType;

    /**
     * 用户类别 0.学生,1.老师
     */
    private Byte userType;

    /**
     * 登录账号
     */
    private String loginAccount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态:0.禁用;1.启用
     */
    private Byte status;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateDate;

    /**
     * 创建人
     */
    private String createUserId;

    /**
     * 更新人
     */
    private String updateUserId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComputerModel() {
        return computerModel;
    }

    public void setComputerModel(String computerModel) {
        this.computerModel = computerModel == null ? null : computerModel.trim();
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem == null ? null : operationSystem.trim();
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor == null ? null : processor.trim();
    }

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard == null ? null : motherboard.trim();
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram == null ? null : ram.trim();
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(String hardDisk) {
        this.hardDisk = hardDisk == null ? null : hardDisk.trim();
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard == null ? null : graphicsCard.trim();
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor == null ? null : monitor.trim();
    }

    public String getOpticalDrive() {
        return opticalDrive;
    }

    public void setOpticalDrive(String opticalDrive) {
        this.opticalDrive = opticalDrive == null ? null : opticalDrive.trim();
    }

    public String getSoundCard() {
        return soundCard;
    }

    public void setSoundCard(String soundCard) {
        this.soundCard = soundCard == null ? null : soundCard.trim();
    }

    public String getNetworkCard() {
        return networkCard;
    }

    public void setNetworkCard(String networkCard) {
        this.networkCard = networkCard == null ? null : networkCard.trim();
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery == null ? null : battery.trim();
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion == null ? null : systemVersion.trim();
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName == null ? null : computerName.trim();
    }

    public String getSystemInstallationDate() {
        return systemInstallationDate;
    }

    public void setSystemInstallationDate(String systemInstallationDate) {
        this.systemInstallationDate = systemInstallationDate == null ? null : systemInstallationDate.trim();
    }

    public String getSystemStartupTime() {
        return systemStartupTime;
    }

    public void setSystemStartupTime(String systemStartupTime) {
        this.systemStartupTime = systemStartupTime == null ? null : systemStartupTime.trim();
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer == null ? null : manufacturer.trim();
    }

    public String getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(String systemLanguage) {
        this.systemLanguage = systemLanguage == null ? null : systemLanguage.trim();
    }

    public String getSystemStructure() {
        return systemStructure;
    }

    public void setSystemStructure(String systemStructure) {
        this.systemStructure = systemStructure == null ? null : systemStructure.trim();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress == null ? null : macAddress.trim();
    }

    public String getMicrophoneEquipment() {
        return microphoneEquipment;
    }

    public void setMicrophoneEquipment(String microphoneEquipment) {
        this.microphoneEquipment = microphoneEquipment == null ? null : microphoneEquipment.trim();
    }

    public String getSpeakerEquipment() {
        return speakerEquipment;
    }

    public void setSpeakerEquipment(String speakerEquipment) {
        this.speakerEquipment = speakerEquipment == null ? null : speakerEquipment.trim();
    }

    public String getCameraEquipment() {
        return cameraEquipment;
    }

    public void setCameraEquipment(String cameraEquipment) {
        this.cameraEquipment = cameraEquipment == null ? null : cameraEquipment.trim();
    }

    public String getTabletDevice() {
        return tabletDevice;
    }

    public void setTabletDevice(String tabletDevice) {
        this.tabletDevice = tabletDevice == null ? null : tabletDevice.trim();
    }

    public String getLastTestTime() {
        return lastTestTime;
    }

    public void setLastTestTime(String lastTestTime) {
        this.lastTestTime = lastTestTime == null ? null : lastTestTime.trim();
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType == null ? null : deviceType.trim();
    }

    public Byte getUserType() {
        return userType;
    }

    public void setUserType(Byte userType) {
        this.userType = userType;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount == null ? null : loginAccount.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId == null ? null : updateUserId.trim();
    }
}