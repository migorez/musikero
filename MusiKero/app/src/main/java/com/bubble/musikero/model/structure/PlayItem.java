package com.bubble.musikero.model.structure;

/**
 * Created by Miguel on 31/07/2017.
 * Abstract class of which its heirs will shares a lot of its methods and attributes.
 */

public abstract class PlayItem {

    // abstract class cannot instanciate. Protected constructor so that the class that extends
    // from this is able to generate a public constructor.
    protected PlayItem() {
    }

    public abstract String getDisplayName();

    public abstract String getPlaybackDurationTime();

    public abstract Long getPlaybackDurationMills();

    public abstract Integer getContentCount();

    public abstract String getItemType();

}
