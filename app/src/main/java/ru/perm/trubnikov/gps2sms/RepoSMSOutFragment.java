package ru.perm.trubnikov.gps2sms;

public class RepoSMSOutFragment extends RepoSMSInFragment {
    @Override
    protected String getSMSSource() {
        return "sent";
    }
}
