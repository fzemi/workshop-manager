<script setup>
import { ref, watch, onMounted, onUnmounted } from 'vue';

const props = defineProps({
    /**
     * HTML template content fetched from backend
     */
    templateHtml: {
        type: String,
        required: true,
    },
    /**
     * Document field values (flat object with keys like 'client.fullName')
     */
    modelValue: {
        type: Object,
        required: true,
    },
});

const emit = defineEmits(['update:modelValue']);

const iframeRef = ref(null);
const iframeContent = ref('');

/**
 * Build the full HTML document for iframe with field values filled in
 */
function buildIframeContent() {
    if (!props.templateHtml) return '';
    
    const parser = new DOMParser();
    const doc = parser.parseFromString(props.templateHtml, 'text/html');
    
    // Process all data-field elements
    const fields = doc.querySelectorAll('[data-field]');
    
    fields.forEach((field) => {
        const fieldName = field.getAttribute('data-field');
        const dataValue = field.getAttribute('data-value');
        
        if (dataValue !== null) {
            // Boolean field - handle strikethrough
            const boolValue = props.modelValue[fieldName];
            const shouldStrike = (dataValue === 'true' && !boolValue) || 
                                 (dataValue === 'false' && boolValue);
            
            if (shouldStrike) {
                field.classList.add('strikethrough');
            } else {
                field.classList.remove('strikethrough');
            }
            
            // Mark for click handling
            field.setAttribute('data-boolean-field', 'true');
            field.style.cursor = 'pointer';
        } else {
            // Text field - create input with value from modelValue
            const value = props.modelValue[fieldName] !== undefined 
                ? props.modelValue[fieldName] 
                : '';
            
            const input = doc.createElement('input');
            input.type = 'text';
            input.setAttribute('value', value); // Use setAttribute instead of .value
            input.setAttribute('data-field-name', fieldName);
            // Auto-size input based on its content (minimum 1 char)
            input.setAttribute('size', String(value ?? '').length > 0 ? String(value).length : 1);
            input.className = 'document-editable-field ' + (field.className || '');
            
            field.replaceWith(input);
        }
    });
    
    // Add editable field styles and event handling script
    const editableStyles = `
        <style>
            .document-editable-field {
                border: 1px dashed #3b82f6 !important;
                background: rgba(59, 130, 246, 0.05) !important;
                padding: 2px 4px !important;
                font-family: inherit;
                font-size: inherit;
                line-height: inherit;
                min-width: 200px;
                max-width: 100%;
                display: inline-block;
                box-sizing: border-box;
            }
            .document-editable-field:focus {
                outline: none;
                border-color: #2563eb !important;
                background: rgba(59, 130, 246, 0.1) !important;
            }
            [data-boolean-field] {
                cursor: pointer;
                transition: opacity 0.2s;
            }
            [data-boolean-field]:hover {
                opacity: 0.7;
            }
        </style>
    `;
    
    const eventScript = `
        <script>
            function autoSizeInput(input) {
                if (!input || !input.hasAttribute('data-field-name')) return;
                const len = (input.value || '').length;
                // size is character-based, but works well enough for “grow with text”
                input.size = Math.max(1, len);
            }

            // Init size for all editable inputs
            window.addEventListener('load', function() {
                document.querySelectorAll('input[data-field-name]').forEach(autoSizeInput);
            });

            // Notify parent about input changes
            document.addEventListener('input', function(e) {
                if (e.target.hasAttribute('data-field-name')) {
                    autoSizeInput(e.target);
                    window.parent.postMessage({
                        type: 'field-change',
                        fieldName: e.target.getAttribute('data-field-name'),
                        value: e.target.value
                    }, '*');
                }
            });
            
            // Notify parent about boolean field clicks
            document.addEventListener('click', function(e) {
                const el = e.target.closest && e.target.closest('[data-boolean-field]');
                if (el) {
                    // Keep preview responsive without re-rendering iframe
                    el.classList.toggle('strikethrough');
                    window.parent.postMessage({
                        type: 'boolean-toggle',
                        fieldName: el.getAttribute('data-field')
                    }, '*');
                }
            });
        <\/script>
    `;
    
    // Get head and body content
    const headContent = doc.head ? doc.head.innerHTML : '';
    const bodyContent = doc.body ? doc.body.innerHTML : '';
    
    return `<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    ${headContent}
    ${editableStyles}
</head>
<body>
    ${bodyContent}
    ${eventScript}
</body>
</html>`;
}

// Rebuild content only when template changes.
// Rebuilding on every modelValue change re-creates the iframe DOM and drops focus.
watch(
    () => props.templateHtml,
    () => {
        if (props.templateHtml && props.modelValue && Object.keys(props.modelValue).length > 0) {
            iframeContent.value = buildIframeContent();
        }
    },
    { immediate: true }
);

/**
 * Handle messages from iframe
 */
function handleMessage(event) {
    if (!event.data || typeof event.data !== 'object') return;
    
    if (event.data.type === 'field-change') {
        emit('update:modelValue', {
            ...props.modelValue,
            [event.data.fieldName]: event.data.value,
        });
    } else if (event.data.type === 'boolean-toggle') {
        const fieldName = event.data.fieldName;
        emit('update:modelValue', {
            ...props.modelValue,
            [fieldName]: !props.modelValue[fieldName],
        });
    }
}

onMounted(() => {
    window.addEventListener('message', handleMessage);
});

onUnmounted(() => {
    window.removeEventListener('message', handleMessage);
});

/**
 * Get the HTML content for printing (with current field values).
 * Uses the ORIGINAL template and only fills in field values,
 * so no input/editor styles leak into the printed document.
 */
function getHtmlForPrint() {
    if (!iframeRef.value || !iframeRef.value.contentDocument) {
        return props.templateHtml;
    }
    
    // 1. Get current values from live inputs in iframe
    const iframeDoc = iframeRef.value.contentDocument;
    const liveInputs = iframeDoc.querySelectorAll('input[data-field-name]');
    const inputValues = {};
    liveInputs.forEach(input => {
        inputValues[input.getAttribute('data-field-name')] = input.value;
    });
    
    // Also get boolean states from iframe (they may have been toggled)
    const booleanFields = iframeDoc.querySelectorAll('[data-boolean-field]');
    const booleanStates = {};
    booleanFields.forEach(el => {
        const fieldName = el.getAttribute('data-field');
        booleanStates[fieldName] = !el.classList.contains('strikethrough');
    });
    
    // 2. Parse the ORIGINAL template (not the modified iframe DOM)
    const parser = new DOMParser();
    const doc = parser.parseFromString(props.templateHtml, 'text/html');
    
    // 3. Fill in text field values
    const fields = doc.querySelectorAll('[data-field]');
    fields.forEach(field => {
        const fieldName = field.getAttribute('data-field');
        const dataValue = field.getAttribute('data-value');
        
        if (dataValue !== null) {
            // Boolean field - apply strikethrough based on current state
            const boolValue = booleanStates[fieldName] !== undefined 
                ? booleanStates[fieldName] 
                : props.modelValue[fieldName];
            const shouldStrike = (dataValue === 'true' && !boolValue) || 
                                 (dataValue === 'false' && boolValue);
            
            if (shouldStrike) {
                field.classList.add('strikethrough');
            } else {
                field.classList.remove('strikethrough');
            }
        } else {
            // Text field - just set the text content
            const value = inputValues[fieldName] !== undefined 
                ? inputValues[fieldName] 
                : (props.modelValue[fieldName] || '');
            field.textContent = value;
        }
    });
    
    // 4. Return the clean HTML (head + body)
    const headContent = doc.head ? doc.head.innerHTML : '';
    const bodyContent = doc.body ? doc.body.innerHTML : '';
    
    return `<head>${headContent}</head><body>${bodyContent}</body>`;
}

defineExpose({
    getHtmlForPrint,
});
</script>

<template>
    <iframe
        ref="iframeRef"
        :srcdoc="iframeContent"
        class="document-iframe"
        frameborder="0"
        sandbox="allow-scripts allow-same-origin"
    />
</template>

<style>
.document-iframe {
    width: 100%;
    height: 600px;
    background: white;
    border: none;
    overflow: auto;
}
</style>
