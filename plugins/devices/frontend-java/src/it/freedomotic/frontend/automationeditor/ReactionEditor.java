/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.freedomotic.frontend.automationeditor;

import it.freedomotic.app.Freedomotic;
import it.freedomotic.reactions.ReactionPersistence;
import it.freedomotic.reactions.Command;
import it.freedomotic.reactions.Reaction;
import it.freedomotic.reactions.Trigger;
import it.freedomotic.reactions.TriggerPersistence;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author enrico
 */
public class ReactionEditor extends javax.swing.JPanel {

    //private PropertiesPanel_1 panel = new PropertiesPanel_1(0, 1);
    private Reaction reaction;
    private ArrayList<GuessCommandBox> list = new ArrayList<GuessCommandBox>();

    /**
     * Creates new form ReactionEditor
     */
    public ReactionEditor(Reaction reaction) {
        initComponents();
        this.reaction = reaction;
        init();
    }

    public ReactionEditor() {
        initComponents();
        this.reaction = new Reaction();
        init();
    }

    private void init() {
        this.setLayout(new FlowLayout());
        //add trigger widget
        final Trigger trigger = reaction.getTrigger();
        final JButton btnTrigger = new JButton(trigger.getName());
        //btnTrigger.setEnabled(false);
        btnTrigger.setToolTipText(trigger.getDescription());
        btnTrigger.setPreferredSize(new Dimension(300, 30));
        btnTrigger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (trigger != null) {
                    Command c = new Command();
                    c.setName("Edit a trigger");
                    c.setReceiver("app.actuators.nlautomationseditor.nlautomationseditor.in");
                    c.setProperty("editor", "trigger");
                    c.setProperty("editable", trigger.getName()); //the default choice
                    c.setReplyTimeout(Integer.MAX_VALUE);
                    Command reply = Freedomotic.sendCommand(c);
                    String newTrigger = reply.getProperty("edited");
                    if (newTrigger != null) {
                        btnTrigger.setName(TriggerPersistence.getTrigger(newTrigger).getName());
                    }
                    //trigger = null;
                    //setText(INFO_MESSAGE);
                }
            }
        });
        this.add(btnTrigger);
        //add commands widget
        for (Command command : reaction.getCommands()) {
            GuessCommandBox box = new GuessCommandBox(this, command);
            addBox(box);
        }
        //add empty command box to append new commands
        addEmptyBox();
    }

    private void addEmptyBox() {
        GuessCommandBox emptyBox = new GuessCommandBox(this);
        addBox(emptyBox);
        this.validate();
    }

    private void addBox(GuessCommandBox box) {
        this.add(box);
        list.add(box);
        this.validate();
    }

    private void removeBox(GuessCommandBox box) {
        this.remove(box);
        list.remove(box);
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    void onCommandConfirmed(GuessCommandBox box) {
        int index = list.indexOf(box);
        if (index >= reaction.getCommands().size()) { //the last box is now filled
            reaction.getCommands().add(box.getCommand());
            addEmptyBox();
        } else {
            reaction.getCommands().set(index, box.getCommand());
        }
        reaction.setChanged();
        System.out.println(reaction.toString());
    }

    void onCommandCleared(GuessCommandBox box) {
        //int index = list.indexOf(box);
        reaction.getCommands().remove(box.getCommand());
        removeBox(box);
        if (list.size() <= reaction.getCommands().size()) {
            addEmptyBox();
        }
        reaction.setChanged();
        System.out.println(reaction.toString());
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void finalizeEditing() {
        ReactionPersistence.add(reaction);
    }
}