package be.hepl.benbear.demojdbc;

import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.commons.streams.UncheckedLambda;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePickerImpl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

public class UpdateDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel realContent;
    private Table<?> table;
    private List<Class> listClass;
    private List<Component> listInput;

    public UpdateDialog(Table<?> table, Object obj) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.table = table;
        this.listClass = new ArrayList<>();
        this.listInput = new ArrayList<>();

        List<Field> listField = Arrays.stream(table.getTableClass().getDeclaredFields())
            .filter(f -> !f.isSynthetic())
            .filter(f -> !Modifier.isTransient(f.getModifiers()))
            .peek(f -> f.setAccessible(true))
            .collect(Collectors.toList());

        realContent.setLayout(new GridLayout(listField.size(), 2));

        listField.forEach(
            UncheckedLambda.consumer(field -> {
                Class c = field.getType();
                realContent.add(new Label(field.getName()));
                Component component;
                if (c.equals(String.class)) {
                    component = new JTextField((String) field.get(obj));
                } else if (c.equals(int.class)) {
                    JSpinner spinner = new JSpinner();
                    spinner.setValue(field.getInt(obj));
                    component = spinner;
                } else {
                    JDatePicker jDatePicker = new JDateComponentFactory().createJDatePicker();
                    component = (Component) jDatePicker;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime((Date) field.get(obj));
                    ((DateModel<Calendar>) jDatePicker.getModel()).setValue(calendar);
                }
                listClass.add(c);
                listInput.add(component);
                realContent.add(component);
            }, ex -> {
                throw new RuntimeException(ex);
            })
        );

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
            e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    private void onOK() {
        try {
            updateNewInstanceHelper(table);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        dispose();
    }

    private <T> void updateNewInstanceHelper(Table<T> table) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = table
            .getTableClass()
            .getConstructor(listClass.toArray(new Class<?>[listClass.size()]));

        Object[] values = listInput.stream()
            .map(component -> {
                Class<?> c = component.getClass();
                if(c.equals(JTextField.class)) {
                    return ((JTextField) component).getText();
                } else if(c.equals(JSpinner.class)) {
                    return (int) ((JSpinner) component).getValue();
                } else {
                    return new Date(((Calendar)((JDatePickerImpl) component).getModel().getValue()).getTimeInMillis());
                }
            })
            .toArray();

        table.update(constructor.newInstance(values));
    }

    private void onCancel() {
        dispose();
    }
}
