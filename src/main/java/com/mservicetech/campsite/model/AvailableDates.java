package com.mservicetech.campsite.model;

import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableDates  {

    private java.util.List<String> datelist;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private String comment;

    public AvailableDates () {
    }

    @JsonProperty("datelist")
    public java.util.List<String> getDatelist() {
        return datelist;
    }

    public void setDatelist(java.util.List<String> datelist) {
        this.datelist = datelist;
    }

    @JsonProperty("startDate")
    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public java.time.LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(java.time.LocalDate endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AvailableDates AvailableDates = (AvailableDates) o;

        return Objects.equals(datelist, AvailableDates.datelist) &&
               Objects.equals(startDate, AvailableDates.startDate) &&
               Objects.equals(endDate, AvailableDates.endDate) &&
               Objects.equals(comment, AvailableDates.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datelist, startDate, endDate, comment);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AvailableDates {\n");
        sb.append("    datelist: ").append(toIndentedString(datelist)).append("\n");        sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");        sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");        sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
