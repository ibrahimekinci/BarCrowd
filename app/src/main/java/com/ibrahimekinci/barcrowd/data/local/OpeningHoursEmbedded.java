package com.ibrahimekinci.barcrowd.data.local;

/**
 * An embedded class to store opening hours directly in the VenueEntity table.
 */
public class OpeningHoursEmbedded {

    // Naming convention (e.g., ohMonday) prevents conflicts
    private String ohMonday;
    private String ohTuesday;
    private String ohWednesday;
    private String ohThursday;
    private String ohFriday;
    private String ohSaturday;
    private String ohSunday;

    public OpeningHoursEmbedded() {
    }

    // --- Getters ---
    public String getOhMonday() {
        return ohMonday;
    }

    public String getOhTuesday() {
        return ohTuesday;
    }

    public String getOhWednesday() {
        return ohWednesday;
    }

    public String getOhThursday() {
        return ohThursday;
    }

    public String getOhFriday() {
        return ohFriday;
    }

    public String getOhSaturday() {
        return ohSaturday;
    }

    public String getOhSunday() {
        return ohSunday;
    }

    // --- Setters ---
    public void setOhMonday(String ohMonday) {
        this.ohMonday = ohMonday;
    }

    public void setOhTuesday(String ohTuesday) {
        this.ohTuesday = ohTuesday;
    }

    public void setOhWednesday(String ohWednesday) {
        this.ohWednesday = ohWednesday;
    }

    public void setOhThursday(String ohThursday) {
        this.ohThursday = ohThursday;
    }

    public void setOhFriday(String ohFriday) {
        this.ohFriday = ohFriday;
    }

    public void setOhSaturday(String ohSaturday) {
        this.ohSaturday = ohSaturday;
    }

    public void setOhSunday(String ohSunday) {
        this.ohSunday = ohSunday;
    }
}