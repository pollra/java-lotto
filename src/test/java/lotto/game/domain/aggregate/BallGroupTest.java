package lotto.game.domain.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import lotto.game.domain.vo.Ball;
import lotto.game.exception.IllegalBallNumberException;
import lotto.io.domain.aggregate.InputTextGroup;
import lotto.io.domain.vo.InputText;
import lotto.io.exception.IllegalInputTextException;
import lotto.io.exception.IllegalInputTextGroupException;
import lotto.io.exception.IllegalInputTextListException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BallGroupTest {

	private List<Ball> makeBallList(InputTextGroup inputTextGroup) throws IllegalBallNumberException {
		List<Ball> balls = new ArrayList<>();
		for (InputText inputText : inputTextGroup.inputTexts()) {
			balls.add(Ball.of(inputText));
		}
		return balls;
	}

	@DisplayName("4-1-2-2-1.`generate()` : 입력된 정보를 토대로 BallGroup을 생성한다.")
	@ParameterizedTest(name = "{index} - text:[{0}], compareText:[{1}], exceptedIsEqual:{2}")
	@Order(1)
	@CsvSource(value = {"1, 2, 3, 4, 5, 6;1,2,3,4,5,6;true", "6,5,4,3,2,1;1,2,3,4,5,6;true",
			"45, 44, 43;43,44,45;true", "1,1,1,1,1,1;1;true", "1,2,3,1,2,3;1,2,3,1,2,3;false",
			"1,2,3,1,2,3;1,2,3;true"}, delimiter = ';')
	void generate(String text, String compareText, boolean exceptedIsEqual) throws
			IllegalInputTextException, IllegalBallNumberException,
			IllegalInputTextListException, IllegalInputTextGroupException {
		//given
		InputTextGroup inputTextGroup = InputText.generate(text).splitByComma();
		InputTextGroup inputCompareTextGroup = InputText.generate(compareText).splitByComma();
		List<Ball> compareBallList = makeBallList(inputCompareTextGroup);

		//when
		if (exceptedIsEqual) {
			//then - exceptedIsEqual:true
			assertThat(BallGroup.generate(inputTextGroup).balls()).containsSequence(compareBallList);
			return;
		}
		//then - exceptedIsEqual:false
		assertThat(BallGroup.generate(inputTextGroup).balls()).doesNotContainSequence(compareBallList);
	}

	@DisplayName("4-1-2-2-1.generate() - throw Exception")
	@ParameterizedTest(name = "{index} - text:[{0}]")
	@Order(2)
	@CsvSource(value = {"0, 1, 2, 3, 4, 5, 6", "1,,2,3", "45, 44, 43, 69", ",,,,,", "` `", "``"}, delimiter = ';')
	void generateWithThrowException(String text) {
		//given

		//when

		//then
		assertThatThrownBy(() -> {
			InputTextGroup inputTextGroup = InputText.generate(text).splitByComma();
			BallGroup.generate(inputTextGroup);
		}).isInstanceOfAny(IllegalInputTextException.class,
			IllegalInputTextListException.class, IllegalBallNumberException.class);
	}

	@DisplayName("4-1-2-2-1.generate() - throw Exception")
	@Test
	@Order(3)
	void generateWithThrowException() throws IllegalInputTextListException {
		//given
		InputTextGroup nullInputTextGroup = null;

		//when

		//then
		assertThatThrownBy(() -> BallGroup.generate(nullInputTextGroup))
			.isInstanceOfAny(IllegalInputTextListException.class, IllegalInputTextGroupException.class);
	}

	@DisplayName("4-1-2-2-2.`allOfBalls()` : 모든 볼의 정보를 초기화하여 static 영역에 가지고 있는다.")
	@Test
	@Order(4)
	void allOfBalls() throws IllegalInputTextException, IllegalBallNumberException {
		//given
		List<Ball> compareBalls = new ArrayList<>();
		for (int i = Ball.EFFECTIVE_MIN_NUMBER; i <= Ball.EFFECTIVE_MAX_NUMBER; i++) {
			InputText inputText = InputText.generate(String.valueOf(i));
			compareBalls.add(Ball.of(inputText));
		}

		//when

		//then
		assertThat(BallGroup.allOfBalls()).containsSequence(compareBalls);
	}

	@DisplayName("4-1-1-2-1.`isContainBall()` : 공 그룹에 공이 포함되었는지 여부")
	@ParameterizedTest(name = "{index} - text:[{0}], ballText[{1}], expectedIsContain:{2}")
	@Order(5)
	@CsvSource(value = {"1,2,3,4,5,6;1;true", "6,5,4,3,2,1;2;true", "1,2,3,3,4,5,6;7;false",
		"1, 21, 31, 45, 41, 11;45;true"}, delimiter = ';')
	void isContainsBall(String text, String ballText, boolean expectedIsContain) {
		//given
		InputTextGroup inputTextGroup = InputText.generate(text).splitByComma();

		InputText inputText = InputText.generate(ballText);

		//when
		BallGroup ballGroup = BallGroup.generate(inputTextGroup);
		Ball ball = Ball.of(inputText);

		//then
		assertThat(ballGroup.isContainBall(ball)).isEqualTo(expectedIsContain);
	}
}
