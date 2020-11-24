package com.sad.jetpack.architecture.componentization.api2;

public interface IPCLauncher extends IPCMessageSender{

    IPCLauncher transmissionConfig(IPCMessageTransmissionConfig config);

    IPCLauncher transmissionConfig(IPCTarget target, int destType);

    IPCLauncher transmissionConfig(String url, int processorMode, int destType);

}
