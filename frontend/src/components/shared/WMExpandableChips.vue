<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
    /**
     * Array of items to display
     * Each item should have 'id' and 'label' properties
     */
    items: {
        type: Array,
        required: true,
        default: () => [],
    },
    /**
     * Maximum number of chips to show before collapsing
     */
    maxVisible: {
        type: Number,
        default: 2,
    },
    /**
     * Entity type for navigation ('client', 'vehicle')
     */
    entityType: {
        type: String,
        default: null,
        validator: (value) => value === null || ['client', 'vehicle'].includes(value),
    },
    /**
     * Chip severity/color
     */
    severity: {
        type: String,
        default: 'secondary',
    },
});

const expanded = ref(false);

const visibleItems = computed(() => {
    if (expanded.value || props.items.length <= props.maxVisible) {
        return props.items;
    }
    return props.items.slice(0, props.maxVisible);
});

const hiddenCount = computed(() => {
    if (expanded.value) return 0;
    return Math.max(0, props.items.length - props.maxVisible);
});

function toggle() {
    expanded.value = !expanded.value;
}
</script>

<template>
    <div class="flex flex-wrap gap-1 items-center" v-if="items.length > 0">
        <template v-for="item in visibleItems" :key="item.id">
            <!-- If entityType is provided, make it clickable -->
            <WMEntityLink
                v-if="entityType"
                :id="item.id"
                :label="item.label"
                :entityType="entityType"
            />
            <!-- Otherwise just show as chip -->
            <Tag
                v-else
                :value="item.label"
                :severity="severity"
                rounded
            />
        </template>

        <!-- Show more/less button -->
        <Button
            v-if="items.length > maxVisible"
            :label="expanded ? 'Mniej' : `+${hiddenCount}`"
            size="small"
            severity="secondary"
            text
            rounded
            @click="toggle"
        />
    </div>
    <span v-else class="text-surface-400">—</span>
</template>
