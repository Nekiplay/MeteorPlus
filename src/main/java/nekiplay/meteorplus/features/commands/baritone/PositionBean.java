package nekiplay.meteorplus.features.commands.baritone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@AllArgsConstructor
@Data
public class PositionBean {
	@NonNull private Coordinate first;
	@NonNull private Coordinate second;
}
