package ro.infotop.journeytoself.listener;

import ro.infotop.journeytoself.model.userModel.User;

public interface FragmentCallback {
    void call(User user, int resultCode);
}
