package olejka.meteorplus.events;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import meteordevelopment.meteorclient.utils.misc.Pool;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreBoardRenderEvent {
	private static final Pool<ScoreBoardRenderEvent> INSTANCE = new Pool<>(ScoreBoardRenderEvent::new);

	public Text title;
	public ArrayList<Text> text;

	public MatrixStack matrices;
	public ScoreboardObjective objective;

	public static ScoreBoardRenderEvent get(MatrixStack matrices, ScoreboardObjective objective) {
		ScoreBoardRenderEvent event = INSTANCE.get();
		event.matrices = matrices;
		event.objective = objective;
		event.title = objective.getDisplayName();
		event.text = new ArrayList<>();
		Scoreboard scoreboard = event.objective.getScoreboard();
		Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(event.objective);
		List<ScoreboardPlayerScore> list = (List)collection.stream().filter((score) -> {
			return score.getPlayerName() != null && !score.getPlayerName().startsWith("#");
		}).collect(Collectors.toList());
		if (list.size() > 15) {
			collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
		} else {
			collection = list;
		}
		ScoreboardPlayerScore scoreboardPlayerScore;
		MutableText text2;
		List<Pair<ScoreboardPlayerScore, Text>> list2 = Lists.newArrayListWithCapacity(((Collection)collection).size());
		Iterator<ScoreboardPlayerScore> var11 = collection.iterator();
		while (var11.hasNext()) {
			scoreboardPlayerScore = (ScoreboardPlayerScore)var11.next();
			Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
			text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
			event.text.add(text2);
		}

		return event;
	}
}
