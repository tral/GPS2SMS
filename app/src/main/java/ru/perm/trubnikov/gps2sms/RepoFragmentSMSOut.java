package ru.perm.trubnikov.gps2sms;

public class RepoFragmentSMSOut extends RepoFragmentSMSIn {

    @Override
    protected String getSMSSource() {
        return "sent";
    }
}


