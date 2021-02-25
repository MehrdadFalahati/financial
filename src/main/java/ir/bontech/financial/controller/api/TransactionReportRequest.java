package ir.bontech.financial.controller.api;

import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
public class TransactionReportRequest {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String accountNumber;
    private String fromDate;
    private String toDate;

    public Date parseFromDate() {
        try {
            return FORMAT.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Date parseToDate() {
        try {
            return FORMAT.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
