package cn.onlyhi.client.request;

import cn.onlyhi.common.request.BaseRequest;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/2/27.
 */
public class SaveDeviceInfoRequest extends BaseRequest {
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

    public String getComputerModel() {
        return computerModel;
    }

    public void setComputerModel(String computerModel) {
        this.computerModel = computerModel;
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public void setOperationSystem(String operationSystem) {
        this.operationSystem = operationSystem;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getMotherboard() {
        return motherboard;
    }

    public void setMotherboard(String motherboard) {
        this.motherboard = motherboard;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(String hardDisk) {
        this.hardDisk = hardDisk;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(String graphicsCard) {
        this.graphicsCard = graphicsCard;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    public String getOpticalDrive() {
        return opticalDrive;
    }

    public void setOpticalDrive(String opticalDrive) {
        this.opticalDrive = opticalDrive;
    }

    public String getSoundCard() {
        return soundCard;
    }

    public void setSoundCard(String soundCard) {
        this.soundCard = soundCard;
    }

    public String getNetworkCard() {
        return networkCard;
    }

    public void setNetworkCard(String networkCard) {
        this.networkCard = networkCard;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getSystemInstallationDate() {
        return systemInstallationDate;
    }

    public void setSystemInstallationDate(String systemInstallationDate) {
        this.systemInstallationDate = systemInstallationDate;
    }

    public String getSystemStartupTime() {
        return systemStartupTime;
    }

    public void setSystemStartupTime(String systemStartupTime) {
        this.systemStartupTime = systemStartupTime;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSystemLanguage() {
        return systemLanguage;
    }

    public void setSystemLanguage(String systemLanguage) {
        this.systemLanguage = systemLanguage;
    }

    public String getSystemStructure() {
        return systemStructure;
    }

    public void setSystemStructure(String systemStructure) {
        this.systemStructure = systemStructure;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getMicrophoneEquipment() {
        return microphoneEquipment;
    }

    public void setMicrophoneEquipment(String microphoneEquipment) {
        this.microphoneEquipment = microphoneEquipment;
    }

    public String getSpeakerEquipment() {
        return speakerEquipment;
    }

    public void setSpeakerEquipment(String speakerEquipment) {
        this.speakerEquipment = speakerEquipment;
    }

    public String getCameraEquipment() {
        return cameraEquipment;
    }

    public void setCameraEquipment(String cameraEquipment) {
        this.cameraEquipment = cameraEquipment;
    }

    public String getTabletDevice() {
        return tabletDevice;
    }

    public void setTabletDevice(String tabletDevice) {
        this.tabletDevice = tabletDevice;
    }

    @Override
    public String toString() {
        return "SaveDeviceInfoRequest{" +
                "computerModel='" + computerModel + '\'' +
                ", operationSystem='" + operationSystem + '\'' +
                ", processor='" + processor + '\'' +
                ", motherboard='" + motherboard + '\'' +
                ", ram='" + ram + '\'' +
                ", hardDisk='" + hardDisk + '\'' +
                ", graphicsCard='" + graphicsCard + '\'' +
                ", monitor='" + monitor + '\'' +
                ", opticalDrive='" + opticalDrive + '\'' +
                ", soundCard='" + soundCard + '\'' +
                ", networkCard='" + networkCard + '\'' +
                ", battery='" + battery + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", computerName='" + computerName + '\'' +
                ", systemInstallationDate='" + systemInstallationDate + '\'' +
                ", systemStartupTime='" + systemStartupTime + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", systemLanguage='" + systemLanguage + '\'' +
                ", systemStructure='" + systemStructure + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", microphoneEquipment='" + microphoneEquipment + '\'' +
                ", speakerEquipment='" + speakerEquipment + '\'' +
                ", cameraEquipment='" + cameraEquipment + '\'' +
                ", tabletDevice='" + tabletDevice + '\'' +
                "} " + super.toString();
    }
}
