import tensorflow as tf
from tensorflow import keras
import matplotlib.pyplot as plt

(train_images, train_labels), (test_images, test_labels) = tf.keras.datasets.mnist.load_data()

train_images = train_images.reshape((60000, 28, 28, 1)).astype('float32') / 255.0
test_images = test_images.reshape((10000, 28, 28, 1)).astype('float32') / 255.0

model = keras.models.Sequential([
    keras.layers.Conv2D(32, (3, 3), activation='relu', input_shape=(28, 28, 1)),
    keras.layers.MaxPooling2D((2, 2)),
    keras.layers.Conv2D(64, (3, 3), activation='relu'),
    keras.layers.MaxPooling2D((2, 2)),
    keras.layers.Conv2D(64, (3, 3), activation='relu'),
    keras.layers.Flatten(),
    keras.layers.Dense(64, activation='relu'),
    keras.layers.Dense(10, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

batch_size = 2048  # 128
epoch_size = 5
validation_interval = 10_000 // batch_size


class PlotCallback(keras.callbacks.Callback):
    def __init__(self):
        super().__init__()
        self.train_losses = []
        self.test_losses = []
        self.train_accuracies = []
        self.test_accuracies = []

    def on_train_batch_end(self, batch, logs=None):
        if batch > 0 and batch % validation_interval == 0 and logs is not None:
            test_loss, test_acc = self.model.evaluate(test_images, test_labels, verbose=0)
            self.train_losses.append(logs['loss'])
            self.test_losses.append(test_loss)
            self.train_accuracies.append(logs['accuracy'])
            self.test_accuracies.append(test_acc)


plotCallback = PlotCallback()

history = model.fit(
    train_images,
    train_labels,
    batch_size=batch_size,
    epochs=epoch_size,
    validation_data=(test_images, test_labels),
    verbose=1,
    callbacks=[plotCallback]
)

model.save('task1/mnist_model.h5')

train_losses = history.history['loss']
test_losses = history.history['val_loss']
train_accuracies = history.history['accuracy']
test_accuracies = history.history['val_accuracy']

print(f'Loss: {train_losses}')
print(f'Validation Loss: {test_losses}')
print(f'Accuracy: {train_accuracies}')
print(f'Validation Accuracy: {test_accuracies}')

epochs = range(1, len(train_losses) + 1)

plt.title('Learning Curve using History')
plt.plot(epochs, train_losses, label='Training Loss')
plt.plot(epochs, test_losses, label='Test Loss')
plt.plot(epochs, train_accuracies, label='Training Accuracy')
plt.plot(epochs, test_accuracies, label='Test Accuracy')

plt.legend()
plt.savefig('task1/history.png')
plt.clf()

train_losses = plotCallback.train_losses
test_losses = plotCallback.test_losses
train_accuracies = plotCallback.train_accuracies
test_accuracies = plotCallback.test_accuracies

print("Lengths for plotCallback:", len(train_losses), len(test_losses), len(train_accuracies),
      len(test_accuracies))

epochs = range(1, len(train_losses) + 1)

plt.title('Learning Curve using PlotCallback')
plt.plot(epochs, train_losses, label='Training Loss')
plt.plot(epochs, test_losses, label='Test Loss')
plt.plot(epochs, train_accuracies, label='Training Accuracy')
plt.plot(epochs, test_accuracies, label='Test Accuracy')

plt.legend()
plt.savefig('task1/plotCallback.png')
plt.clf()
