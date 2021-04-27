package suncor.com.android.ui.common;

import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.util.ArrayDeque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;

@Navigator.Name("keep_state_fragment")
public class KeepStateNavigator extends FragmentNavigator {
    private final int containerId;
    private FragmentManager manager;
    private Context context;
    ArrayDeque<Integer> mBackStack;

    Fragment activeTab;


    public KeepStateNavigator(@NonNull Context context, @NonNull FragmentManager manager, int containerId) {
        super(context, manager, containerId);
        this.manager = manager;
        this.context = context;
        this.containerId = containerId;
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mBackStack");
            field.setAccessible(true);
            mBackStack = (ArrayDeque<Integer>) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public NavDestination navigate(@NonNull Destination destination, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        if (!destination.getArguments().containsKey("root"))
            return super.navigate(destination, args, navOptions, navigatorExtras);

        while (mBackStack.size() > 1) {
            popBackStack();
        }

        mBackStack.clear();
        String tag = destination.getLabel().toString();
        FragmentTransaction transaction = manager.beginTransaction();

        if (activeTab != null) {
            transaction.detach(activeTab);
        }

        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            String className = destination.getClassName();
            fragment = instantiateFragment(context, manager, className, args);
            fragment.setArguments(args);
            transaction.add(containerId, fragment, tag);
        } else {
            fragment.setArguments(args);
            transaction.attach(fragment);
        }

        mBackStack.add(destination.getId());

        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commit();

        activeTab = fragment;

        return destination;
    }
}