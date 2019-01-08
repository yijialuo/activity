package com.activity.demo.bean;

import java.util.List;

/**
 * Created by yijialuo on 2019/1/8.
 */
public class VacationForm extends BaseForm {
    //休假开始日期
    private String startDate;
    //休假结束日期
    private String endDate;
    //天数
    private int days;
    //类型
    private int vacationType;
    //原因
    private String reason;
    @Override
    public void createFormFields(List<FormField> fields) {
        fields.add(super.getFormField("startDate","请假开始日期",startDate));
        fields.add(super.getFormField("endDate","请假结束日期",endDate));
        fields.add(super.getFormField("days","休假天速",String.valueOf(days)));
        fields.add(super.getFormField("vacationType","请假类型",getVacationType(this.vacationType)));
        fields.add(super.getFormField("reason","原因",reason));
    }

    //获取请假类型
    private String getVacationType(int vacationType){
        if(vacationType==1){
            return "事假";
        }else if (vacationType==2){
            return "年假";
        }else if (vacationType==3){
            return "病假";
        }
        return "";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getVacationType() {
        return vacationType;
    }

    public void setVacationType(int vacationType) {
        this.vacationType = vacationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
