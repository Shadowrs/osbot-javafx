package org.shadowrs.osbot.fxutil;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@RequiredArgsConstructor
public class ProgressListener {

    @Getter
    @NonNull
    final Feedback feedback;

    double percent;

    public void onProgressUpdate(double percent) {
        feedback.println(format("%s%%", percent));
        this.percent = percent;
    }

    public void updateDownloadSpeed(double speed) {
        feedback.println(format("%s%%", speed));
    }

    public double getCurrentProgress() {
        return percent;
    }

    public void updateMessageAndProgress(String format, double progress) {
        feedback.println(format("%s : %s%%", format, progress));
        this.percent = progress;
    }
}
