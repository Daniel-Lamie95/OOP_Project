import java.util.UUID;
public interface Searchable {
    Memory findMemory(UUID id);
    Reminder findReminder(UUID id);
    Relative findRelative(UUID id);
}
