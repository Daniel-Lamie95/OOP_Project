public interface Searchable {
    Memory findMemory(String name);
    Reminder findReminder(String name);
    Relative findRelative(String name);
}
