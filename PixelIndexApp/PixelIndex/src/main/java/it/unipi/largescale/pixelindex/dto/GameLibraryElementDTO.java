package it.unipi.largescale.pixelindex.dto;

import java.time.LocalDate;

public class GameLibraryElementDTO extends GamePreviewDTO {
    private LocalDate addedDate;

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        String result = super.toString();
        result += (" Added Date: " + addedDate);
        return result;
    }
}