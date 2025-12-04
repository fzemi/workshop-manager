<script setup>
import { ref } from 'vue';
import { useConfirm } from 'primevue/useconfirm';

const props = defineProps({
    /**
     * Title text for the confirmation dialog
     */
    header: {
        type: String,
        default: 'Potwierdź usunięcie',
    },
    /**
     * Message to display in the dialog
     */
    message: {
        type: String,
        default: 'Czy na pewno chcesz usunąć ten element? Tej operacji nie można cofnąć.',
    },
    /**
     * Accept button label
     */
    acceptLabel: {
        type: String,
        default: 'Usuń',
    },
    /**
     * Reject button label
     */
    rejectLabel: {
        type: String,
        default: 'Anuluj',
    },
});

const emit = defineEmits(['confirm', 'cancel']);

const confirm = useConfirm();

/**
 * Show the confirmation dialog
 * @param {Object} data - Data to pass to the confirm event
 */
function show(data = null) {
    confirm.require({
        header: props.header,
        message: props.message,
        icon: 'pi pi-exclamation-triangle',
        rejectProps: {
            label: props.rejectLabel,
            severity: 'secondary',
            outlined: true,
        },
        acceptProps: {
            label: props.acceptLabel,
            severity: 'danger',
        },
        accept: () => {
            emit('confirm', data);
        },
        reject: () => {
            emit('cancel', data);
        },
    });
}

// Expose show method to parent components
defineExpose({ show });
</script>

<template>
    <ConfirmDialog />
</template>
