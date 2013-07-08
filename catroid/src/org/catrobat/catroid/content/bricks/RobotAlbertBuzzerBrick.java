/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class RobotAlbertBuzzerBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient EditText editValue;
	private Formula value;

	protected Object readResolve() {
		/*
		 * if (motor != null) {
		 * motorEnum = Motor.valueOf(motor);
		 * }
		 */
		return this;
	}

	public RobotAlbertBuzzerBrick(Sprite sprite, int value) {
		this.sprite = sprite;
		this.value = new Formula(value);
	}

	public RobotAlbertBuzzerBrick(Sprite sprite, Formula value) {
		this.sprite = sprite;
		this.value = value;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_ROBOT_ALBERT;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_robot_albert_buzzer_action, null);
		TextView textValue = (TextView) prototypeView.findViewById(R.id.robot_albert_buzzer_action_frequency_text_view);
		textValue.setText(String.valueOf(value.interpretInteger(sprite)));
		//TODO set the spinner Value to A
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new RobotAlbertBuzzerBrick(getSprite(), value.clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_robot_albert_buzzer_action, null);
		setCheckboxView(R.id.brick_robot_albert_buzzer_action_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textValue = (TextView) view.findViewById(R.id.robot_albert_buzzer_action_frequency_text_view);
		editValue = (EditText) view.findViewById(R.id.robot_albert_buzzer_action_frequency_edit_text);
		value.setTextFieldId(R.id.robot_albert_buzzer_action_frequency_edit_text);
		value.refreshTextField(view);

		textValue.setVisibility(View.GONE);
		editValue.setVisibility(View.VISIBLE);

		editValue.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, value);
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_robot_albert_buzzer_action_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.RobotAlbertBuzzerAction(sprite, value));
		return null;
	}
}
