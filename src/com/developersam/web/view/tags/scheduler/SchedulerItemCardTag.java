package com.developersam.web.view.tags.scheduler;

import com.developersam.web.devsuit.tags.components.card.CardTag;
import com.developersam.web.devsuit.tags.components.card.CardTextBorderedTag;
import com.developersam.web.devsuit.tags.components.card.CardTextTag;
import com.developersam.web.model.scheduler.SchedulerItem;

import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * A card consists of all the information of a scheduler item.
 */
public class SchedulerItemCardTag extends CardTag {

    private SchedulerItem schedulerItem;

    /**
     * Initialize the tag by binding a scheduler item object to it.
     * The object will contain sufficient info to be displayed properly.
     * @param schedulerItem a scheduler item
     */
    public void setSchedulerItem(SchedulerItem schedulerItem) {
        this.schedulerItem = schedulerItem;
        setId("scheduler-item-"+schedulerItem.getKeyString());
        setTitle(schedulerItem.getDescription());
    }

    /**
     * Print the deadline and description of the scheduler item in a nice manner.
     * @throws JspException error
     * @throws IOException error
     */
    private void printSchedulerItemContent() throws JspException, IOException {
        CardTextBorderedTag cardTextTagForDeadline = new CardTextBorderedTag();
        cardTextTagForDeadline.setParent(this);
        cardTextTagForDeadline.setBodyContent("Deadline: " + schedulerItem.getDeadline());
        cardTextTagForDeadline.doTag();
    }

    @Override
    protected void printBodyContent() throws JspException, IOException {
        printTitle();
        printSchedulerItemContent();
    }
}