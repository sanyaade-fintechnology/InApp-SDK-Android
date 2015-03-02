package de.payleven.inappdemo;

import android.support.annotation.NonNull;

import de.payleven.inappsdk.errors.ValidationError;
import de.payleven.inappsdk.errors.causes.ErrorCause;

/**
 * Utilities class
 */
public class Util {

    public static String getErrorFormatted(@NonNull final ValidationError error) {
        StringBuffer stringBuffer = new StringBuffer();

        for (ErrorCause errorCause : error.getErrorCauses()) {
            stringBuffer.append(errorCause.getErrorCode().getErrorCode());
            stringBuffer.append(" ");
            stringBuffer.append(errorCause.getErrorMessage());
            stringBuffer.append("\n");
        }
        return stringBuffer.toString();
    }
}
