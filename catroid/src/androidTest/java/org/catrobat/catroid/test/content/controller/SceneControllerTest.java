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

package org.catrobat.catroid.test.content.controller;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.SceneController;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.utils.Utils.buildPath;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

@RunWith(AndroidJUnit4.class)
public class SceneControllerTest {

	private static final String BACK_PACK_SCENES_PATH = Utils.buildPath(
			Constants.DEFAULT_ROOT,
			Constants.BACKPACK_DIRECTORY,
			Constants.SCENES_DIRECTORY);
	private Project project;
	private Scene scene;

	@Before
	public void setUp() throws IOException {
		clearBackPack();
		createProject();
	}

	@After
	public void tearDown() throws IOException {
		deleteProject();
		clearBackPack();
	}

	@Test
	public void testCopyScene() throws IOException {
		SceneController controller = new SceneController();
		Scene copy = controller.copy(scene, project);

		assertEquals(1, project.getSceneList().size());
		assertEquals(scene.getSpriteList().size(), copy.getSpriteList().size());

		for (int i = 0; i < copy.getSpriteList().size(); i++) {
			assertEquals(
					scene.getSpriteList().get(i).getLookList().size(),
					copy.getSpriteList().get(i).getLookList().size());

			assertEquals(
					scene.getSpriteList().get(i).getSoundList().size(),
					copy.getSpriteList().get(i).getSoundList().size());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfScripts(),
					copy.getSpriteList().get(i).getNumberOfScripts());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfBricks(),
					copy.getSpriteList().get(i).getNumberOfBricks());
		}

		assertLookFileExistsInScene(copy.getSpriteList().get(1).getLookList().get(0).getFileName(), copy);
		assertSoundFileExistsInScene(copy.getSpriteList().get(1).getSoundList().get(0).getFileName(), copy);
	}

	@Test
	public void testDeleteScene() throws IOException {
		SceneController controller = new SceneController();
		String deletedSceneDir = scene.getPath();

		controller.delete(scene);

		assertEquals(1, project.getSceneList().size());
		assertFileDoesNotExist(deletedSceneDir);
	}

	@Test
	public void testPackScene() throws IOException {
		SceneController controller = new SceneController();
		Scene packedScene = controller.pack(scene);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedScenes().size());

		assertEquals(buildPath(BACK_PACK_SCENES_PATH, packedScene.getName()), packedScene.getPath());
		assertFileExists(packedScene.getPath());

		assertEquals(scene.getSpriteList().size(), packedScene.getSpriteList().size());

		for (int i = 0; i < packedScene.getSpriteList().size(); i++) {
			assertEquals(
					scene.getSpriteList().get(i).getLookList().size(),
					packedScene.getSpriteList().get(i).getLookList().size());

			assertEquals(
					scene.getSpriteList().get(i).getSoundList().size(),
					packedScene.getSpriteList().get(i).getSoundList().size());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfScripts(),
					packedScene.getSpriteList().get(i).getNumberOfScripts());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfBricks(),
					packedScene.getSpriteList().get(i).getNumberOfBricks());
		}

		assertLookFileExistsInScene(
				packedScene.getSpriteList().get(1).getLookList().get(0).getFileName(),
				packedScene);

		assertSoundFileExistsInScene(
				packedScene.getSpriteList().get(1).getSoundList().get(0).getFileName(),
				packedScene);
	}

	@Test
	public void testUnpackScene() throws IOException {
		SceneController controller = new SceneController();

		Scene packedScene = controller.pack(scene);
		Scene unpackedScene = controller.unpack(packedScene, project);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedScenes().size());

		assertEquals(1, project.getSceneList().size());

		assertEquals(scene.getSpriteList().size(), unpackedScene.getSpriteList().size());

		for (int i = 0; i < unpackedScene.getSpriteList().size(); i++) {
			assertEquals(
					scene.getSpriteList().get(i).getLookList().size(),
					unpackedScene.getSpriteList().get(i).getLookList().size());

			assertEquals(
					scene.getSpriteList().get(i).getSoundList().size(),
					unpackedScene.getSpriteList().get(i).getSoundList().size());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfScripts(),
					unpackedScene.getSpriteList().get(i).getNumberOfScripts());

			assertEquals(
					scene.getSpriteList().get(i).getNumberOfBricks(),
					unpackedScene.getSpriteList().get(i).getNumberOfBricks());
		}

		assertLookFileExistsInScene(
				unpackedScene.getSpriteList().get(1).getLookList().get(0).getFileName(),
				unpackedScene);

		assertSoundFileExistsInScene(
				unpackedScene.getSpriteList().get(1).getSoundList().get(0).getFileName(),
				unpackedScene);
	}

	private void assertLookFileExistsInScene(String fileName, Scene scene) {
		assertFileExists(scene.getPath(), Constants.IMAGE_DIRECTORY, fileName);
	}

	private void assertSoundFileExistsInScene(String fileName, Scene scene) {
		assertFileExists(scene.getPath(), Constants.SOUND_DIRECTORY, fileName);
	}

	private void clearBackPack() throws IOException {
		File backPackDir = new File(BACK_PACK_SCENES_PATH);
		if (backPackDir.exists()) {
			StorageHandler.deleteDir(BACK_PACK_SCENES_PATH);
		}
		backPackDir.mkdirs();
	}

	private void createProject() {
		project = new Project(InstrumentationRegistry.getTargetContext(), "SpriteControllerTest");
		scene = project.getDefaultScene();
		ProjectManager.getInstance().setCurrentProject(project);

		Sprite sprite = new Sprite("testSprite");
		scene.addSprite(sprite);

		StartScript script = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(0, 0);
		script.addBrick(placeAtBrick);
		sprite.addScript(script);

		File imageFile = FileTestUtils.saveFileToProject(
				project.getName(), scene.getName(),
				"red_image.bmp",
				org.catrobat.catroid.test.R.raw.red_image,
				InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		sprite.getLookList().add(new LookData("testLook", imageFile.getName()));

		File soundFile = FileTestUtils.saveFileToProject(
				project.getName(), ProjectManager.getInstance().getCurrentScene().getName(), "longsound.mp3",
				org.catrobat.catroid.test.R.raw.longsound, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND
		);

		sprite.getSoundList().add(new SoundInfo("testSound", soundFile.getName()));

		StorageHandler.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageHandler.deleteDir(buildProjectPath(project.getName()));
		}
	}
}
