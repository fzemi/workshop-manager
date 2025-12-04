<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
    /**
     * Page title
     */
    title: {
        type: String,
        required: true,
    },
    /**
     * Route path for create button (optional)
     * If not provided, create button is hidden
     */
    createRoute: {
        type: String,
        default: null,
    },
    /**
     * Label for create button
     */
    createLabel: {
        type: String,
        default: 'Utwórz',
    },
});

const router = useRouter();

function navigateToCreate() {
    if (props.createRoute) {
        router.push(props.createRoute);
    }
}
</script>

<template>
    <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-semibold text-surface-800 dark:text-surface-100">
            {{ title }}
        </h1>
        <Button
            v-if="createRoute"
            :label="createLabel"
            icon="pi pi-plus"
            @click="navigateToCreate"
        />
    </div>
</template>
