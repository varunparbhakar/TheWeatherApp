package edu.uw.tcss450.varpar.weatherapp.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * View model for message count badges.
 */
public class NewMessageCountViewModel extends ViewModel {

    /** Current new message count. */
    private final MutableLiveData<Integer> mNewMessageCount;

    /**
     * Constructor sets to 0 value.
     */
    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(0);
    }

    /**
     * Add observer to network responses.
     * @param owner Lifecycle parent.
     * @param observer NewMessageCountViewModel.class
     */
    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mNewMessageCount.observe(owner, observer);
    }

    /**
     * Increase count of new messages.
     */
    public void increment() {
        mNewMessageCount.setValue(mNewMessageCount.getValue() + 1);
    }

    /**
     * Reset count of new messages.
     */
    public void reset() {
        mNewMessageCount.setValue(0);
    }
}
