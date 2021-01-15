package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedList;
import java.util.List;

final class InternalComponentChain implements IComponentChain{

    private IResponse response;
    private String id="";
    private List<Object> units =new LinkedList<>();
    private int currIndex=-1;
    private IComponentChain.IComponentChainTerminalCallback terminalCallback;
    private boolean outsideIntercepted=false;

    public void setResponse(IResponse response) {
        this.response = response;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTerminalCallback(IComponentChainTerminalCallback terminalCallback) {
        this.terminalCallback = terminalCallback;
    }

    protected InternalComponentChain(List<Object> units,boolean outsideIntercepted ){
        this.units=units;
        this.outsideIntercepted=outsideIntercepted;
    }

    @Override
    public IResponse response() {
        return this.response;
    }

    @Override
    public String parentId() {
        return this.id;
    }

    @Override
    public void proceedResponse(IResponse response, String id) throws Exception {

        if (currIndex>units.size()-1){
            LogcatUtils.e(">>>回溯链已经结束");
            return;
        }
        this.response=response;
        this.id=id;
        currIndex++;
        //LogcatUtils.e(">>>回溯链信息：currIdx="+currIndex+",size="+units.size());
        if (currIndex>units.size()-1 && terminalCallback!=null){
            LogcatUtils.e(">>>回溯链末端回调");
            terminalCallback.onLast(response,id,outsideIntercepted);
        }
        else {
            Object o=units.get(currIndex);
            if (o instanceof IResponseBackTrackable){
                LogcatUtils.e(">>>回溯链调用：rid="+((IResponseBackTrackable) o).backTrackableId()+",response="+response+",id="+id);
                ((IResponseBackTrackable) o).onBackTrackResponse(this);
            }
            else {
                LogcatUtils.e(">>>非回溯链类型");
            }
        }
    }
}
