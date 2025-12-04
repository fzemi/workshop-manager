<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
    /**
     * Entity ID
     */
    id: {
        type: [String, Number],
        required: true,
    },
    /**
     * Display label
     */
    label: {
        type: String,
        required: true,
    },
    /**
     * Entity type for routing ('client', 'vehicle', 'repair')
     */
    entityType: {
        type: String,
        required: true,
        validator: (value) => ['client', 'vehicle', 'repair'].includes(value),
    },
});

const router = useRouter();

const routeMap = {
    client: '/clients',
    vehicle: '/vehicles',
    repair: '/repairs',
};

function navigateToDetail() {
    const basePath = routeMap[props.entityType];
    router.push(`${basePath}/${props.id}`);
}
</script>

<template>
    <a
        href="#"
        class="text-primary-600 hover:text-primary-800 hover:underline cursor-pointer font-medium"
        @click.prevent="navigateToDetail"
    >
        {{ label }}
    </a>
</template>
