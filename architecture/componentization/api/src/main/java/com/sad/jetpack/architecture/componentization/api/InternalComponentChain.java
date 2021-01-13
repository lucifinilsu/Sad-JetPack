package com.sad.jetpack.architecture.componentization.api;

import java.util.LinkedList;
import java.util.List;

final class InternalComponentChain implements IComponentChain{

    private IResponse response;
    private String id="";
    private List<Object> units =new LinkedList<>();
    private int currIndex=0;
    private IComponentChain.IComponentChainTerminalCallback terminalCallback;

    public void setTerminalCallback(IComponentChainTerminalCallback terminalCallback) {
        this.terminalCallback = terminalCallback;
    }

    protected InternalComponentChain(List<Object> units){
        this.units=units;
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
            return;
        }
        this.response=response;
        this.id=id;
        if (currIndex==units.size()-1 && terminalCallback!=null){
            terminalCallback.onLast(response,id);
        }
        else {
            Object o=units.get(currIndex);
            if (o instanceof IResponseBackTrackable){
                ((IResponseBackTrackable) o).onBackTrackResponse(this);
            }
        }
        currIndex++;
    }
}
