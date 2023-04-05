package co.intentservice.chatui.sample;

public class string_variable_status {

    private String status = "Tor is starting...";
    private ChangeListener listener1;
    private ChangeListener listener2;
    private ChangeListener listener3;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (listener1 != null) {listener1.onChange();}
        if (listener2 != null) {listener2.onChange();}
        if (listener2 != null) {listener3.onChange();}
    }

    public ChangeListener getListener1() {
        return listener1;
    }
    public ChangeListener getListener2() {
        return listener2;
    }
    public ChangeListener getListener3() {
        return listener3;
    }

    public void setListener1(ChangeListener listener) {
        this.listener1 = listener;
    }
    public void setListener2(ChangeListener listener) {
        this.listener2 = listener;
    }
    public void setListener3(ChangeListener listener) {
        this.listener3 = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

}