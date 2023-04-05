package co.intentservice.chatui.sample;

public class boolean_variable_chat_update {

    private boolean boo = false;
    private ChangeListener listener;

    public boolean isUpdated() {
        return boo;
    }

    public void setUpdate(boolean boo) {
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