<script setup>
import { ref, computed, useSlots } from 'vue';
import { FilterMatchMode } from '@primevue/core/api';

const props = defineProps({
    /**
     * Array of data to display
     */
    data: {
        type: Array,
        required: true,
    },
    /**
     * Column definitions
     * Each column: { field: string, header: string, sortable?: boolean, style?: string }
     */
    columns: {
        type: Array,
        required: true,
    },
    /**
     * Whether the table is loading
     */
    loading: {
        type: Boolean,
        default: false,
    },
    /**
     * Placeholder text for search input
     */
    searchPlaceholder: {
        type: String,
        default: 'Szukaj...',
    },
    /**
     * Fields to search in (for global filter)
     */
    globalFilterFields: {
        type: Array,
        default: () => [],
    },
    /**
     * Number of rows per page
     */
    rowsPerPage: {
        type: Number,
        default: 10,
    },
    /**
     * Whether to show action column
     */
    showActions: {
        type: Boolean,
        default: true,
    },
    /**
     * Empty message when no data
     */
    emptyMessage: {
        type: String,
        default: 'Brak danych',
    },
    /**
     * Row key field for selection
     */
    dataKey: {
        type: String,
        default: 'id',
    },
});

const emit = defineEmits(['view', 'edit', 'delete']);

const slots = useSlots();

const filters = ref({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
});

const computedGlobalFilterFields = computed(() => {
    if (props.globalFilterFields.length > 0) {
        return props.globalFilterFields;
    }
    return props.columns.map((col) => col.field);
});

function onView(row) {
    emit('view', row);
}

function onEdit(row) {
    emit('edit', row);
}

function onDelete(row) {
    emit('delete', row);
}
</script>

<template>
    <div class="wm-data-table">
        <!-- Search Bar -->
        <div class="flex justify-end mb-4">
            <IconField>
                <InputIcon class="pi pi-search" />
                <InputText
                    v-model="filters['global'].value"
                    :placeholder="searchPlaceholder"
                    class="w-full md:w-80"
                />
            </IconField>
        </div>

        <!-- Data Table -->
        <DataTable
            :value="data"
            :loading="loading"
            :paginator="true"
            :rows="rowsPerPage"
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :filters="filters"
            :globalFilterFields="computedGlobalFilterFields"
            :dataKey="dataKey"
            removableSort
            stripedRows
            size="small"
            paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
            currentPageReportTemplate="Pokazano {first} do {last} z {totalRecords} wpisów"
        >
            <template #empty>
                <div class="text-center py-4 text-surface-500">
                    {{ emptyMessage }}
                </div>
            </template>

            <template #loading>
                <div class="text-center py-4">
                    <i class="pi pi-spin pi-spinner text-2xl"></i>
                </div>
            </template>

            <!-- Dynamic Columns -->
            <Column
                v-for="col in columns"
                :key="col.field"
                :field="col.field"
                :header="col.header"
                :sortable="col.sortable !== false"
                :style="col.style"
            >
                <template #body="slotProps">
                    <!-- Check if slot exists for this column -->
                    <slot
                        :name="`column-${col.field}`"
                        :data="slotProps.data"
                        :field="col.field"
                    >
                        {{ slotProps.data[col.field] }}
                    </slot>
                </template>
            </Column>

            <!-- Actions Column -->
            <Column
                v-if="showActions"
                header="Akcje"
                :exportable="false"
                style="min-width: 10rem"
                frozen
                alignFrozen="right"
            >
                <template #body="slotProps">
                    <div class="flex gap-2">
                        <Button
                            icon="pi pi-eye"
                            severity="info"
                            text
                            rounded
                            v-tooltip.top="'Podgląd'"
                            @click="onView(slotProps.data)"
                        />
                        <Button
                            icon="pi pi-pencil"
                            severity="success"
                            text
                            rounded
                            v-tooltip.top="'Edytuj'"
                            @click="onEdit(slotProps.data)"
                        />
                        <Button
                            icon="pi pi-trash"
                            severity="danger"
                            text
                            rounded
                            v-tooltip.top="'Usuń'"
                            @click="onDelete(slotProps.data)"
                        />
                    </div>
                </template>
            </Column>
        </DataTable>
    </div>
</template>
