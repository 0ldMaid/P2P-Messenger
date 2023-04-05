package co.intentservice.chatui.sample;

public class boolean_variable_feed_connection {

    private boolean boo = false;
    private ChangeListener listener;

    public boolean isConnected() {
        return boo;
    }

    public void setConnected(boolean boo) {
        this.boo = boo;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

}