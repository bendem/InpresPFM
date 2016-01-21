package be.hepl.benbear.demojdbc;

import be.hepl.benbear.commons.db.Table;
import be.hepl.benbear.commons.reflection.FieldReflection;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.impl.JDatePickerImpl;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

public class InsertDialog<T> extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel realContent;
    private final Table<T> table;
    private final FieldReflection<T> fieldReflection;
    private final List<Component> listInput;

    public InsertDialog(Table<T> table) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.table = table;
        this.fieldReflection = new FieldReflection<>(table.getTableClass(), FieldReflection.NON_SYNTHETIC, FieldReflection.NON_TRANSIENT);
        this.listInput = new ArrayList<>(fieldReflection.count());

        realContent.setLayout(new GridLayout(fieldReflection.count(), 2));

        fieldReflection.getTypeMap().forEach((name, type) -> {
            realContent.add(new JLabel(name));
            Component component;
            if (type.equals(String.class)) {
                component = new JTextField();
            } else if (type.equals(int.class)) {
                component = new JSpinner();
            } else {
                component = (Component) new JDateComponentFactory().createJDatePicker();
            }
            listInput.add(component);
            realContent.add(component);
        });

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

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
        Constructor<T> constructor;
        try {
            constructor = table
                .getTableClass()
                .getConstructor(fieldReflection.getTypes().toArray(Class<?>[]::new));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        Object[] values = listInput.stream()
            .map(component -> {
                if (component instanceof JTextField) {
                    return ((JTextField) component).getText();
                } else if (component instanceof JSpinner) {
                    return (int) ((JSpinner) component).getValue();
                } else {
                    return new Date(((Calendar) ((JDatePickerImpl) component).getModel().getValue()).getTimeInMillis());
                }
            })
            .toArray();

        try {
            table.insert(constructor.newInstance(values)).get();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Insert error: " + e.getMessage());
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel1, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        buttonOK = new JButton();
        buttonOK.setText("OK");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonOK, gbc);
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(buttonCancel, gbc);
        realContent = new JPanel();
        realContent.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(realContent, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
