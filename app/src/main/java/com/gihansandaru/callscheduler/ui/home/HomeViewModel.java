package com.gihansandaru.callscheduler.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gihansandaru.callscheduler.models.CallLogData;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<CallLogData>> mCallLogList;

    public HomeViewModel() {
        mCallLogList = new MutableLiveData<>();
    }

    public LiveData<List<CallLogData>> getCallLogList() {
        return mCallLogList;
    }

    public void setCallLogList(List<CallLogData> callLogList) {
        mCallLogList.postValue(callLogList);
    }

    public void filterUnknownNumbers(boolean isChecked, List<CallLogData> callLogData) {
        List<CallLogData> newData = new ArrayList<>();
        if(isChecked){
            for (CallLogData data : callLogData) {
                if(data.getName() == null){
                    newData.add(data);
                }
            }
        }else{
            newData.addAll(callLogData);
        }
        mCallLogList.postValue(newData);
    }
}