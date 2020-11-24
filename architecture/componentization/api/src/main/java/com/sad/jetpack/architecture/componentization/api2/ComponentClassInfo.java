package com.sad.jetpack.architecture.componentization.api2;

import android.os.Parcel;
import android.os.Parcelable;

public class ComponentClassInfo implements Parcelable {

    private String url="";
    private String resPath="";
    private String className="";
    private String description="";
    private int version=1;
    public ComponentClassInfo(){}

    protected ComponentClassInfo(Parcel in) {
        url = in.readString();
        resPath = in.readString();
        className = in.readString();
        description = in.readString();
        version = in.readInt();
    }

    public static final Creator<ComponentClassInfo> CREATOR = new Creator<ComponentClassInfo>() {
        @Override
        public ComponentClassInfo createFromParcel(Parcel in) {
            return new ComponentClassInfo(in);
        }

        @Override
        public ComponentClassInfo[] newArray(int size) {
            return new ComponentClassInfo[size];
        }
    };

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResPath() {
        return resPath;
    }

    public void setResPath(String resPath) {
        this.resPath = resPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(resPath);
        dest.writeString(className);
        dest.writeString(description);
        dest.writeInt(version);
    }
}
