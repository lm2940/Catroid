/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.physics;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class PhysicsBaseTest extends InstrumentationTestCase {

	protected Sprite sprite;
	protected PhysicsWorld physicsWorld;

	protected Project project;
	private String rectangle125x125FileName;
	protected File rectangle125x125File;
	private static final int RECTANGLE125X125_RES_ID = R.raw.rectangle_125x125;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();
		rectangle125x125FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_125x125.png");

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);

		physicsWorld = project.getDefaultScene().getPhysicsWorld();
		sprite = new Sprite("TestSprite");
		sprite.look = new PhysicsLook(sprite, physicsWorld);

		project.getDefaultScene().addSprite(sprite);

		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		rectangle125x125File = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME, project.getDefaultScene().getName(),
				rectangle125x125FileName, RECTANGLE125X125_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);

		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle125x125File);
		sprite.look.setLookData(lookdata);
		sprite.setActionFactory(new ActionPhysicsFactory());

		assertTrue("getLookData is null", sprite.look.getLookData() != null);

		stabilizePhysicsWorld(physicsWorld);
	}

	@Override
	protected void tearDown() throws Exception {
		sprite = null;
		physicsWorld = null;

		project = null;
		rectangle125x125FileName = null;
		rectangle125x125File = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public static void stabilizePhysicsWorld(PhysicsWorld physicsWorld) {
		for (int index = 0; index < PhysicsWorld.STABILIZING_STEPS; index++) {
			physicsWorld.step(0.0f);
		}
	}
}
