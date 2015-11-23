package org.rapla.gui.internal.edit.fields;

import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rapla.components.util.Assert;
import org.rapla.entities.dynamictype.Attribute;
import org.rapla.entities.dynamictype.AttributeType;
import org.rapla.framework.RaplaContext;
import org.rapla.gui.EditField;
/**
 * Here is an enhancement for faster searching methods. This class
 * generates a text field for the user to type in search criteria.
 * The length is set to 15 so that it will fit smoothly with the pre-existing
 * filter button.
 * @author Jackson, Claxton
 *
 */
public class SearchTextField extends TextField implements ActionListener,FocusListener,KeyListener, MultiEditField, SetGetField<String>
{

	public SearchTextField(RaplaContext context) 
    {
        this( context,"", 1, 15);
    }
    
    public SearchTextField(RaplaContext context,String fieldName) 
    {
        this( context,fieldName, 1, 15);
    }
        
    public SearchTextField(RaplaContext sm,String fieldName, int rows, int columns) 
    {
    	super(sm, fieldName, rows, columns);
    }
    
    protected void fireFilterChanged() {
        if (listenerList.size() == 0)
            return;
        ChangeEvent evt = new ChangeEvent(this);
        ChangeListener[] listeners = getChangeListeners();
        for (int i = 0;i<listeners.length; i++) {
            listeners[i].stateChanged(evt);
        }
    }
    
    class RuleRow implements ChangeListener, ItemListener{
        Object ruleValue;
        JLabel ruleLabel;
        JComponent operatorComponent;
        AbstractEditField field;
        Attribute attribute;

        public void stateChanged(ChangeEvent e) {
            fireFilterChanged();
        }
        
        public void itemStateChanged(ItemEvent e) {
            fireFilterChanged();
        }
        
		RuleRow(Attribute attribute,String operator,Object ruleValue) {
            this.attribute = attribute;
            this.ruleValue = ruleValue;
            ruleLabel = new JLabel();
            ruleLabel.setText(attribute.getName().getName(getI18n().getLang()));
            createField( attribute );
            // we can cast here, because we tested in createField
            @SuppressWarnings("unchecked")
			SetGetField<Object> setGetField = (SetGetField<Object>)field;
			setGetField.setValue(ruleValue);
            field.addChangeListener( this);
            setOperatorValue(operator);
            
            if ( operatorComponent instanceof ItemSelectable)
            {
                ((ItemSelectable)operatorComponent).addItemListener(this);
            }
            
        }


        public String getOperatorValue() {
        	/*
            AttributeType type = attribute.getType();
            if (type.equals(AttributeType.ALLOCATABLE) || type.equals(AttributeType.CATEGORY) || type.equals(AttributeType.BOOLEAN) )
                return "is";
            if (type.equals(AttributeType.STRING)) {
            	int index = ((JComboBox)operatorComponent).getSelectedIndex();
            	if (index == 0)
            	*/
                return "contains";
                /*
            	if (index == 1)
            		return "starts";
            }
            if (type.equals(AttributeType.DATE) || type.equals(AttributeType.INT)) {
                int index = ((JComboBox)operatorComponent).getSelectedIndex();
                if (index == 0)
                    return "<";
                if (index == 1)
                    return "=";
                if (index == 2)
                    return ">";
                if (index == 3)
                    return "<>";
                if (index == 4)
                    return "<=";
                if (index == 5)
                    return ">=";
                
            }
            Assert.notNull(field,"Unknown AttributeType" + type);
            return null;
            */
        }

        private void setOperatorValue(String operator) {
            AttributeType type = attribute.getType();
            if ((type.equals(AttributeType.DATE) || type.equals(AttributeType.INT)))
            {
                if (operator == null)
                    operator = "<";
                JComboBox box = (JComboBox)operatorComponent;
                if (operator.equals("<"))
                    box.setSelectedIndex(0);
                if (operator.equals("=") || operator.equals("is"))
                    box.setSelectedIndex(1);
                if (operator.equals(">"))
                    box.setSelectedIndex(2);
                if (operator.equals("<>"))
                    box.setSelectedIndex(3);
                if (operator.equals("<="))
                    box.setSelectedIndex(4);
                if (operator.equals(">="))
                    box.setSelectedIndex(5);
                
            }
        }

        private EditField createField(Attribute attribute) {
            operatorComponent = null;
            AttributeType type = attribute.getType();
            // used for static testing of the field type 
            @SuppressWarnings("unused")
            SetGetField test;
            RaplaContext context = getContext();
            /*
            if (type.equals(AttributeType.ALLOCATABLE))
            {
                operatorComponent = new JLabel("");
                DynamicType dynamicTypeConstraint = (DynamicType)attribute.getConstraint( ConstraintIds.KEY_DYNAMIC_TYPE);
                AllocatableSelectField newField = new AllocatableSelectField(context, dynamicTypeConstraint);
                field = newField;
                test = newField;
               
            }
            else if (type.equals(AttributeType.CATEGORY))
            {
                operatorComponent = new JLabel("");
                Category rootCategory = (Category)attribute.getConstraint(ConstraintIds.KEY_ROOT_CATEGORY);
                if (rootCategory.getDepth() > 2) {
                    Category defaultCategory = (Category) attribute.defaultValue();
                    CategorySelectField newField = new CategorySelectField(context,rootCategory,defaultCategory);
					field = newField;
					test = newField;
                } else {
                    CategoryListField newField = new CategoryListField(context,rootCategory);
					field = newField;
					test = newField;
                }
            }
            */
            //else if (type.equals(AttributeType.STRING))
            {
                TextField newField = new TextField(context);
				field = newField;
				test = newField;
                @SuppressWarnings("unchecked")
				DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {
                		 getString("filter.contains")
                        ,getString("filter.starts")
                    });
                @SuppressWarnings("unchecked")
				JComboBox jComboBox = new JComboBox(model);
				operatorComponent = jComboBox;
            }
            /*
            else if (type.equals(AttributeType.INT))
            {
                LongField newField = new LongField(context);
				field = newField;
				test = newField;
                @SuppressWarnings("unchecked")
				DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {
                    getString("filter.is_smaller_than")
                    ,getString("filter.equals")
                    ,getString("filter.is_greater_than")
                    ,getString("filter.not_equals")
                    ,getString("filter.smaller_or_equals")
                    ,getString("filter.greater_or_equals")
                });
                @SuppressWarnings("unchecked")
				JComboBox jComboBox = new JComboBox(model);
				operatorComponent = jComboBox;
                
            }
            else if (type.equals(AttributeType.DATE))
            {
                DateField newField = new DateField(context);
				field = newField;
				test = newField;
                @SuppressWarnings("unchecked")
				DefaultComboBoxModel model = new DefaultComboBoxModel(new String[] {
                    getString("filter.earlier_than")
                    ,getString("filter.equals")
                    ,getString("filter.later_than")
                    ,getString("filter.not_equals")
                }); 
                @SuppressWarnings("unchecked")
				JComboBox jComboBox = new JComboBox(model);
				operatorComponent = jComboBox;            }
            else if (type.equals(AttributeType.BOOLEAN))
            {
                operatorComponent = new JLabel("");
                BooleanField newField = new BooleanField(context);
				field = newField;
				test = newField;
                ruleValue = new Boolean(false);
            }
           */
            Assert.notNull(field,"Unknown AttributeType");
            return field;
        }
}
}
